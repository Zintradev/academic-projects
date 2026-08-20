program TestSeries;

{$mode objfpc}{$H+}

uses
  SysUtils, USeries;

var
  TestRecord: TSeries;
  LoadedRecord: TSeries;
  TestFile: string;
  TempCopyFile: string;

procedure Assert(Condition: Boolean; const Message: string);
begin
  if not Condition then
  begin
    Writeln('FAIL: ', Message);
    Halt(1);
  end;
end;

begin
  TestFile := 'test_series.dat';
  TempCopyFile := 'test_copy.tmp';
  
  // Clean up previous runs if any
  if FileExists(TestFile) then
    DeleteFile(TestFile);
  if FileExists(TempCopyFile) then
    DeleteFile(TempCopyFile);

  Writeln('========================================');
  Writeln('  Running TSeries Unit Tests...  ');
  Writeln('========================================');

  // Test 1: Record Initialization
  Writeln('Test 1: Initializing record...');
  TestRecord.Initialize('Breaking Bad', 2008, 'Masterpiece', 5000000, 'breaking_bad.jpg');
  Assert(TestRecord.Title = 'Breaking Bad', 'Title initialization mismatch.');
  Assert(TestRecord.Year = 2008, 'Year initialization mismatch.');
  Assert(TestRecord.Reviews = 'Masterpiece', 'Reviews initialization mismatch.');
  Assert(TestRecord.Views = 5000000, 'Views initialization mismatch.');
  Assert(TestRecord.Photo = 'breaking_bad.jpg', 'Photo initialization mismatch.');
  Writeln('-> Pass: Initialization successful.');
  Writeln;

  // Test 2: Saving and Record Count
  Writeln('Test 2: Saving record and checking file count...');
  TestRecord.Save(TestFile, 0);
  Assert(FileExists(TestFile), 'Database file was not created.');
  Assert(GetRecordCount(TestFile) = 1, 'Record count should be 1 after saving.');
  Writeln('-> Pass: Record saved successfully.');
  Writeln;

  // Test 3: Loading and Verifying Values
  Writeln('Test 3: Loading saved record and verifying fields...');
  LoadedRecord.Load(TestFile, 0);
  Assert(LoadedRecord.Title = 'Breaking Bad', 'Loaded Title mismatch.');
  Assert(LoadedRecord.Year = 2008, 'Loaded Year mismatch.');
  Assert(LoadedRecord.Reviews = 'Masterpiece', 'Loaded Reviews mismatch.');
  Assert(LoadedRecord.Views = 5000000, 'Loaded Views mismatch.');
  Assert(LoadedRecord.Photo = 'breaking_bad.jpg', 'Loaded Photo mismatch.');
  Writeln('-> Pass: Record read and verified successfully.');
  Writeln;

  // Test 4: Multiple Records
  Writeln('Test 4: Adding second record...');
  TestRecord.Initialize('Game of Thrones', 2011, 'Very Good', 9999999, 'got.jpg');
  TestRecord.Save(TestFile, 1);
  Assert(GetRecordCount(TestFile) = 2, 'Record count should be 2 after saving second record.');
  
  // Verify second record values
  LoadedRecord.Load(TestFile, 1);
  Assert(LoadedRecord.Title = 'Game of Thrones', 'Loaded Title mismatch for second record.');
  Assert(LoadedRecord.Year = 2011, 'Loaded Year mismatch for second record.');
  Writeln('-> Pass: Multiple records managed successfully.');
  Writeln;

  // Test 5: Deleting Records
  Writeln('Test 5: Deleting a record...');
  // Delete the first record (index 0: Breaking Bad)
  LoadedRecord.Delete(TestFile, TempCopyFile, 0);
  Assert(GetRecordCount(TestFile) = 1, 'Record count should be 1 after deleting a record.');
  
  // Verify that the remaining record is Game of Thrones (originally index 1, now index 0)
  LoadedRecord.Load(TestFile, 0);
  Assert(LoadedRecord.Title = 'Game of Thrones', 'Remaining record should be Game of Thrones.');
  Writeln('-> Pass: Record deletion successful.');
  Writeln;

  // Clean up files created during test
  if FileExists(TestFile) then
    DeleteFile(TestFile);
  if FileExists(TempCopyFile) then
    DeleteFile(TempCopyFile);

  Writeln('========================================');
  Writeln('  All tests PASSED successfully!  ');
  Writeln('========================================');
  Halt(0);
end.
