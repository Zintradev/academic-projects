unit UAddSeries;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Forms, Controls, Graphics, Dialogs, ExtCtrls, StdCtrls,
  ExtDlgs, USeries;

type

  { TFAddSeries }

  TFAddSeries = class(TForm)
    btnSelectCover: TButton;
    btnSave: TButton;
    btnBack: TButton;
    edtName: TEdit;
    edtYear: TEdit;
    edtReviews: TEdit;
    edtViews: TEdit;
    lblFormTitle: TLabel;
    lblViews: TLabel;
    lblReviews: TLabel;
    lblName: TLabel;
    lblYear: TLabel;
    dlgOpenPicture: TOpenPictureDialog;
    imgCover: TImage;
    procedure btnSelectCoverClick(Sender: TObject);
    procedure btnSaveClick(Sender: TObject);
    procedure btnBackClick(Sender: TObject);
    procedure FormClose(Sender: TObject; var CloseAction: TCloseAction);
    procedure FormCreate(Sender: TObject);
  private
    FCurrentSeries: TSeries;
    FPhotoName: string;
  public

  end;

var
  FAddSeries: TFAddSeries;

implementation

{$R *.lfm}

uses
  UMainMenu;

{ TFAddSeries }

procedure TFAddSeries.FormCreate(Sender: TObject);
begin
  FPhotoName := 'blanco.jpg';
end;

procedure TFAddSeries.btnSelectCoverClick(Sender: TObject);
var
  PhotoPath: string;
begin
  if dlgOpenPicture.Execute then
  begin
    PhotoPath := dlgOpenPicture.FileName;
    FPhotoName := ExtractFileName(PhotoPath);
    imgCover.Picture.LoadFromFile(PhotoPath);
    // Save the photo to the project's local images directory
    imgCover.Picture.SaveToFile(IMAGE_DIR + FPhotoName);
  end;
end;

procedure TFAddSeries.btnSaveClick(Sender: TObject);
var
  ValYear, ValViews: Integer;
begin
  // Validation of empty fields
  if (Trim(edtName.Text) = '') or (Trim(edtYear.Text) = '') or (Trim(edtReviews.Text) = '') or (Trim(edtViews.Text) = '') then
  begin
    ShowMessage('Please complete all fields.');
    Exit;
  end;

  // Validation of numeric types
  if not TryStrToInt(Trim(edtYear.Text), ValYear) then
  begin
    ShowMessage('The year must be a valid integer.');
    Exit;
  end;

  if not TryStrToInt(Trim(edtViews.Text), ValViews) then
  begin
    ShowMessage('The number of views must be a valid integer.');
    Exit;
  end;

  // Initialize and save the series record
  FCurrentSeries.Initialize(Trim(edtName.Text), ValYear, Trim(edtReviews.Text), ValViews, FPhotoName);
  FCurrentSeries.Save(DB_FILE, GetRecordCount(DB_FILE));

  ShowMessage('Series saved successfully.');

  // Clear fields for a new entry
  edtName.Text := '';
  edtYear.Text := '';
  edtReviews.Text := '';
  edtViews.Text := '';
  FPhotoName := 'blanco.jpg';
  if FileExists(IMAGE_DIR + 'blanco.jpg') then
    imgCover.Picture.LoadFromFile(IMAGE_DIR + 'blanco.jpg');
end;

procedure TFAddSeries.btnBackClick(Sender: TObject);
begin
  FAddSeries.Hide;
  FMainMenu.Show;
end;

procedure TFAddSeries.FormClose(Sender: TObject; var CloseAction: TCloseAction);
begin
  FMainMenu.Show;
end;

end.
