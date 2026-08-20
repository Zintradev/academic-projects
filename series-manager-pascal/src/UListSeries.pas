unit UListSeries;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Forms, Controls, Graphics, Dialogs, StdCtrls, ExtCtrls,
  ExtDlgs, USeries;

type

  { TFListSeries }

  TFListSeries = class(TForm)
    btnPrevious: TButton;
    btnNext: TButton;
    btnUpdate: TButton;
    btnDelete: TButton;
    btnChangeCover: TButton;
    btnBack: TButton;
    lblTitle: TLabel;
    lstSeries: TListBox;
    dlgOpenPicture: TOpenPictureDialog;
    edtName: TEdit;
    edtYear: TEdit;
    edtReviews: TEdit;
    edtViews: TEdit;
    imgCover: TImage;
    lblName: TLabel;
    lblYear: TLabel;
    lblReviews: TLabel;
    lblViews: TLabel;
    procedure btnPreviousClick(Sender: TObject);
    procedure btnDeleteClick(Sender: TObject);
    procedure btnChangeCoverClick(Sender: TObject);
    procedure btnUpdateClick(Sender: TObject);
    procedure btnNextClick(Sender: TObject);
    procedure btnBackClick(Sender: TObject);
    procedure FormActivate(Sender: TObject);
    procedure lstSeriesClick(Sender: TObject);
    procedure FormClose(Sender: TObject; var CloseAction: TCloseAction);
  private
    FCurrentIndex: Integer;
    FLastIndex: Integer;
    FCurrentSeries: TSeries;
    FPhotoName: string;
  public
    procedure UpdateDetails(APosition: Integer);
    procedure LoadSeriesList;
  end;

var
  FListSeries: TFListSeries;

implementation

{$R *.lfm}

uses
  UMainMenu;

{ TFListSeries }

procedure TFListSeries.LoadSeriesList;
var
  I: Integer;
  TempSeries: TSeries;
  SavedIndex: Integer;
begin
  SavedIndex := lstSeries.ItemIndex;
  lstSeries.Items.Clear;
  FLastIndex := GetRecordCount(DB_FILE) - 1;
  
  for I := 0 to FLastIndex do
  begin
    TempSeries.Load(DB_FILE, I);
    lstSeries.Items.Add(TempSeries.Title);
  end;
  
  // Restore previous selection or select the first one
  if (SavedIndex >= 0) and (SavedIndex < lstSeries.Items.Count) then
    lstSeries.ItemIndex := SavedIndex
  else if lstSeries.Items.Count > 0 then
    lstSeries.ItemIndex := 0;
end;

procedure TFListSeries.UpdateDetails(APosition: Integer);
begin
  FCurrentSeries.Load(DB_FILE, APosition);
  edtName.Text := FCurrentSeries.Title;
  edtYear.Text := IntToStr(FCurrentSeries.Year);
  edtReviews.Text := FCurrentSeries.Reviews;
  edtViews.Text := IntToStr(FCurrentSeries.Views);
  
  if (FCurrentSeries.Photo <> '') and FileExists(IMAGE_DIR + FCurrentSeries.Photo) then
    FPhotoName := FCurrentSeries.Photo
  else
    FPhotoName := 'blanco.jpg';
    
  if FileExists(IMAGE_DIR + FPhotoName) then
    imgCover.Picture.LoadFromFile(IMAGE_DIR + FPhotoName);
end;

procedure TFListSeries.lstSeriesClick(Sender: TObject);
begin
  if lstSeries.ItemIndex >= 0 then
  begin
    FCurrentIndex := lstSeries.ItemIndex;
    UpdateDetails(FCurrentIndex);
  end;
end;

procedure TFListSeries.FormActivate(Sender: TObject);
begin
  LoadSeriesList;
  if lstSeries.ItemIndex >= 0 then
    FCurrentIndex := lstSeries.ItemIndex
  else
    FCurrentIndex := 0;
  UpdateDetails(FCurrentIndex);
end;

procedure TFListSeries.btnPreviousClick(Sender: TObject);
begin
  if FCurrentIndex > 0 then
  begin
    FCurrentIndex := FCurrentIndex - 1;
    lstSeries.ItemIndex := FCurrentIndex;
    UpdateDetails(FCurrentIndex);
  end;
end;

procedure TFListSeries.btnNextClick(Sender: TObject);
begin
  if FCurrentIndex < FLastIndex then
  begin
    FCurrentIndex := FCurrentIndex + 1;
    lstSeries.ItemIndex := FCurrentIndex;
    UpdateDetails(FCurrentIndex);
  end;
end;

procedure TFListSeries.btnDeleteClick(Sender: TObject);
begin
  if GetRecordCount(DB_FILE) = 0 then
  begin
    ShowMessage('No records to delete.');
    Exit;
  end;

  if MessageDlg('Confirm Deletion', 'Are you sure you want to delete this series?', mtConfirmation, [mbYes, mbNo], 0) = mrYes then
  begin
    FCurrentSeries.Delete(DB_FILE, TEMP_FILE, FCurrentIndex);
    
    if FCurrentIndex > 0 then 
      FCurrentIndex := FCurrentIndex - 1;
      
    LoadSeriesList;
    
    if lstSeries.Items.Count > 0 then
    begin
      if FCurrentIndex >= lstSeries.Items.Count then
        FCurrentIndex := lstSeries.Items.Count - 1;
      lstSeries.ItemIndex := FCurrentIndex;
    end
    else
      FCurrentIndex := 0;
      
    UpdateDetails(FCurrentIndex);
  end;
end;

procedure TFListSeries.btnChangeCoverClick(Sender: TObject);
var
  PhotoPath: string;
begin
  if dlgOpenPicture.Execute then
  begin
    PhotoPath := dlgOpenPicture.FileName;
    FPhotoName := ExtractFileName(PhotoPath);
    imgCover.Picture.LoadFromFile(PhotoPath);
    imgCover.Picture.SaveToFile(IMAGE_DIR + FPhotoName);
  end;
end;

procedure TFListSeries.btnUpdateClick(Sender: TObject);
var
  ValYear, ValViews: Integer;
begin
  if GetRecordCount(DB_FILE) = 0 then
  begin
    ShowMessage('No records to modify.');
    Exit;
  end;

  if (Trim(edtName.Text) = '') or (Trim(edtYear.Text) = '') or (Trim(edtReviews.Text) = '') or (Trim(edtViews.Text) = '') then
  begin
    ShowMessage('Please complete all fields.');
    Exit;
  end;

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

  FCurrentSeries.Initialize(Trim(edtName.Text), ValYear, Trim(edtReviews.Text), ValViews, FPhotoName);
  FCurrentSeries.Save(DB_FILE, FCurrentIndex);
  
  LoadSeriesList;
  ShowMessage('Record modified successfully.');
end;

procedure TFListSeries.btnBackClick(Sender: TObject);
begin
  FListSeries.Hide;
  FMainMenu.Show;
end;

procedure TFListSeries.FormClose(Sender: TObject; var CloseAction: TCloseAction);
begin
  FMainMenu.Show;
end;

end.
