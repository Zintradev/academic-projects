unit USeries;

{$codepage UTF8}
{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils;

type
  TString30 = string[30];

  // We use 'object' (Turbo Pascal style) instead of 'class' to
  // enable direct binary persistence to disk using typed files (file of TSeries).
  // In Object Pascal, 'class' instances are heap-allocated and managed as pointers,
  // whereas 'object' instances are stack-allocated and stored contiguously,
  // making it possible to read and write records directly to/from binary files.
  TSeries = object
    Title: TString30;
    Year: Integer;
    Reviews: TString30;
    Views: Integer;
    Photo: TString30;
    procedure Initialize(const ATitle: TString30; AYear: Integer; const AReviews: TString30; AViews: Integer; const APhoto: TString30);
    procedure Save(const APath: string; APosition: Integer);
    procedure Load(const APath: string; APosition: Integer);
    procedure Delete(const APath, ACopyPath: string; APosition: Integer);
  end;

const
  DB_FILE = 'series.dat';
  TEMP_FILE = 'temp.tmp';
  IMAGE_DIR = 'images' + DirectorySeparator;

function GetRecordCount(const APath: string): Integer;

implementation

function GetRecordCount(const APath: string): Integer;
var
  FSource: file of TSeries;
begin
  if FileExists(APath) then
  begin
    Assign(FSource, APath);
    Reset(FSource);
    GetRecordCount := FileSize(FSource);
    Close(FSource);
  end
  else
    GetRecordCount := 0;
end;

procedure TSeries.Initialize(const ATitle: TString30; AYear: Integer; const AReviews: TString30; AViews: Integer; const APhoto: TString30);
begin
  Title := ATitle;
  Year := AYear;
  Reviews := AReviews;
  Views := AViews;
  Photo := APhoto;
end;

procedure TSeries.Save(const APath: string; APosition: Integer);
var
  FDestination: file of TSeries;
begin
  Assign(FDestination, APath);
  if FileExists(APath) then
  begin
    Reset(FDestination);
    Seek(FDestination, APosition);
  end
  else
    Rewrite(FDestination);
  Write(FDestination, Self);
  Close(FDestination);
end;

procedure TSeries.Load(const APath: string; APosition: Integer);
var
  FDestination: file of TSeries;
  TotalRecords: Integer;
begin
  if FileExists(APath) then
  begin
    TotalRecords := GetRecordCount(APath);
    if (TotalRecords > 0) and (APosition >= 0) and (APosition < TotalRecords) then
    begin
      Assign(FDestination, APath);
      Reset(FDestination);
      Seek(FDestination, APosition);
      Read(FDestination, Self);
      Close(FDestination);
    end
    else
    begin
      // Initialize with default values if position is out of bounds
      Title := '';
      Year := 0;
      Reviews := '';
      Views := 0;
      Photo := 'blanco.jpg';
    end;
  end
  else
  begin
    // Initialize with default values if file does not exist
    Title := '';
    Year := 0;
    Reviews := '';
    Views := 0;
    Photo := 'blanco.jpg';
  end;
end;

procedure TSeries.Delete(const APath, ACopyPath: string; APosition: Integer);
var
  FSource, FCopy: file of TSeries;
  I, LastRecordIndex: Integer;
begin
  // Avoid operations if file doesn't exist or is empty
  if not FileExists(APath) or (GetRecordCount(APath) = 0) then
    Exit;

  LastRecordIndex := GetRecordCount(APath) - 1;
  Assign(FSource, APath);
  Reset(FSource);
  Assign(FCopy, ACopyPath);
  Rewrite(FCopy);
  
  for I := 0 to LastRecordIndex do
  begin
    Seek(FSource, I);
    Read(FSource, Self);
    if I <> APosition then
      Write(FCopy, Self);
  end;
  
  Close(FSource);
  Close(FCopy);
  DeleteFile(APath);
  RenameFile(ACopyPath, APath);
end;

end.
