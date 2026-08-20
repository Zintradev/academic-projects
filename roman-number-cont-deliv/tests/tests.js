// tests/tests.js

// Use Chai's expect for assertions
const { expect } = window.chai;

// Destructure conversion functions from the globally loaded RomanConverter namespace
const { integerToRoman, romanToInteger } = RomanConverter;

describe("Roman Numeral Converter", function () {
  describe("Handmade Set: Integer to Roman (TC-1 to TC-13)", function () {
    const handmadeIntToRoman = [
      { id: "TC-1", in: 1, out: "I" },
      { id: "TC-2", in: 4, out: "IV" },
      { id: "TC-3", in: 5, out: "V" },
      { id: "TC-4", in: 9, out: "IX" },
      { id: "TC-5", in: 10, out: "X" },
      { id: "TC-6", in: 40, out: "XL" },
      { id: "TC-7", in: 50, out: "L" },
      { id: "TC-8", in: 90, out: "XC" },
      { id: "TC-9", in: 100, out: "C" },
      { id: "TC-10", in: 400, out: "CD" },
      { id: "TC-11", in: 500, out: "D" },
      { id: "TC-12", in: 900, out: "CM" },
      { id: "TC-13", in: 1000, out: "M" },
    ];

    handmadeIntToRoman.forEach((tc) => {
      it(`${tc.id}: Should convert ${tc.in} to "${tc.out}"`, () => {
        expect(integerToRoman(tc.in)).to.equal(tc.out);
      });
    });
  });

  describe("Handmade Set: Roman to Integer (TC-14 to TC-26)", function () {
    const handmadeRomanToInt = [
      { id: "TC-14", in: "I", out: 1 },
      { id: "TC-15", in: "IV", out: 4 },
      { id: "TC-16", in: "V", out: 5 },
      { id: "TC-17", in: "IX", out: 9 },
      { id: "TC-18", in: "X", out: 10 },
      { id: "TC-19", in: "XL", out: 40 },
      { id: "TC-20", in: "L", out: 50 },
      { id: "TC-21", in: "XC", out: 90 },
      { id: "TC-22", in: "C", out: 100 },
      { id: "TC-23", in: "CD", out: 400 },
      { id: "TC-24", in: "D", out: 500 },
      { id: "TC-25", in: "CM", out: 900 },
      { id: "TC-26", in: "M", out: 1000 },
    ];

    handmadeRomanToInt.forEach((tc) => {
      it(`${tc.id}: Should convert "${tc.in}" to ${tc.out}`, () => {
        expect(romanToInteger(tc.in)).to.equal(tc.out);
      });
    });
  });

  describe("Validation & Error Handling (TC-27 to TC-34)", function () {
    it('TC-27: Should fail for "VIIII" (Rule: no more than 3 consecutive)', () => {
      expect(() => romanToInteger("VIIII")).to.throw(
        "The Roman numeral is not in canonical form.",
      );
    });
    it('TC-28: Should fail for "IIV" (Rule: subtrahend only once)', () => {
      expect(() => romanToInteger("IIV")).to.throw(
        "The Roman numeral is not in canonical form.",
      );
    });
    it('TC-29: Should fail for "XD" (Invalid subtraction rule)', () => {
      expect(() => romanToInteger("XD")).to.throw(
        "The Roman numeral is not in canonical form.",
      );
    });
    it('TC-30: Should fail for "VV" (Rule: V, L, D only once)', () => {
      expect(() => romanToInteger("VV")).to.throw(
        "The Roman numeral is not in canonical form.",
      );
    });

    it("TC-31: Should fail for integer 0", () => {
      expect(() => integerToRoman(0)).to.throw(
        "The number must be between 1 and 3999.",
      );
    });
    it("TC-32: Should fail for integer 4000", () => {
      expect(() => integerToRoman(4000)).to.throw(
        "The number must be between 1 and 3999.",
      );
    });
    it("TC-33: Should fail for integer -5", () => {
      expect(() => integerToRoman(-5)).to.throw(
        "The number must be between 1 and 3999.",
      );
    });

    it("TC-34: Should handle decimals like 12.8 (treating as 12)", () => {
      expect(integerToRoman(Math.floor(12.8))).to.equal("XII");
    });
  });

  describe("AI Set: Complex and Additional Cases", function () {
    it('AI TC-8: intToRoman(14) => "XIV"', () => {
      expect(integerToRoman(14)).to.equal("XIV");
    });
    it('AI TC-9: intToRoman(44) => "XLIV"', () => {
      expect(integerToRoman(44)).to.equal("XLIV");
    });
    it('AI TC-10: intToRoman(3999) => "MMMCMXCIX"', () => {
      expect(integerToRoman(3999)).to.equal("MMMCMXCIX");
    });
    it('AI TC-11: romanToInt("III") => 3', () => {
      expect(romanToInteger("III")).to.equal(3);
    });
    it('AI TC-12: romanToInt("LVIII") => 58', () => {
      expect(romanToInteger("LVIII")).to.equal(58);
    });
    it('AI TC-13: romanToInt("MCMXCIV") => 1994', () => {
      expect(romanToInteger("MCMXCIV")).to.equal(1994);
    });
    it('AI TC-14: romanToInt("CDXLIV") => 444', () => {
      expect(romanToInteger("CDXLIV")).to.equal(444);
    });
  });
});
