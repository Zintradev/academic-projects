unit UMainMenu;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Forms, Controls, Graphics, Dialogs, StdCtrls, ExtCtrls;

type

  { TFMainMenu }

  TFMainMenu = class(TForm)
    btnAddSeries: TButton;
    btnListSeries: TButton;
    imgLogo: TImage;
    lblTitle: TLabel;
    procedure btnAddSeriesClick(Sender: TObject);
    procedure btnListSeriesClick(Sender: TObject);
  private

  public

  end;

var
  FMainMenu: TFMainMenu;

implementation

{$R *.lfm}

uses
  UAddSeries, UListSeries;

{ TFMainMenu }

procedure TFMainMenu.btnAddSeriesClick(Sender: TObject);
begin
  FMainMenu.Hide;
  FAddSeries.Show;
end;

procedure TFMainMenu.btnListSeriesClick(Sender: TObject);
begin
  FMainMenu.Hide;
  FListSeries.Show;
end;

end.
