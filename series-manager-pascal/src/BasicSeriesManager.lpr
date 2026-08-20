program BasicSeriesManager;

{$mode objfpc}{$H+}

uses
  {$IFDEF UNIX}
  cthreads,
  {$ENDIF}
  Interfaces, // this includes the LCL widgetset
  Forms,
  UMainMenu,
  UAddSeries,
  UListSeries,
  USeries;

{$R *.res}

begin
  RequireDerivedFormResource:=True;
  Application.Scaled:=True;
  Application.Initialize;
  Application.CreateForm(TFMainMenu, FMainMenu);
  Application.CreateForm(TFAddSeries, FAddSeries);
  Application.CreateForm(TFListSeries, FListSeries);
  Application.Run;
end.
