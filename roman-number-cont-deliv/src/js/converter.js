/**
 * Roman Numeral Converter module.
 * Encapsulates conversion algorithms and constants to avoid global namespace pollution.
 */
const RomanConverter = (function() {
  /**
   * Mapping of integer values to their corresponding Roman numeral symbols.
   * @type {Array<{value: number, numeral: string}>}
   */
  const ROMAN_NUMERAL_MAPPINGS = [
    { value: 1000, numeral: 'M' },
    { value: 900, numeral: 'CM' },
    { value: 500, numeral: 'D' },
    { value: 400, numeral: 'CD' },
    { value: 100, numeral: 'C' },
    { value: 90, numeral: 'XC' },
    { value: 50, numeral: 'L' },
    { value: 40, numeral: 'XL' },
    { value: 10, numeral: 'X' },
    { value: 9, numeral: 'IX' },
    { value: 5, numeral: 'V' },
    { value: 4, numeral: 'IV' },
    { value: 1, numeral: 'I' }
  ];

  /**
   * Mapping of Roman numeral characters to their integer values.
   * @type {Object<string, number>}
   */
  const ROMAN_CHARACTER_VALUES = {
    'I': 1,
    'V': 5,
    'X': 10,
    'L': 50,
    'C': 100,
    'D': 500,
    'M': 1000
  };

  /**
   * Converts an integer (between 1 and 3999) to its Roman numeral equivalent.
   *
   * @param {number} num - The integer to convert.
   * @returns {string} The Roman numeral representation.
   * @throws {Error} Throws an error if the number is not within the allowed range.
   */
  function integerToRoman(num) {
    // Validate that the number is within the allowed range (1-3999)
    if (num <= 0 || num >= 4000) {
      throw new Error('The number must be between 1 and 3999.');
    }

    let remaining = num;
    let result = '';

    // Loop through each numeral mapping, appending the numeral symbol
    // as many times as possible while subtracting its value from remaining.
    for (const { value, numeral } of ROMAN_NUMERAL_MAPPINGS) {
      while (remaining >= value) {
        result += numeral;
        remaining -= value;
      }
    }

    return result;
  }

  /**
   * Converts a Roman numeral string to its integer equivalent.
   *
   * @param {string} roman - The Roman numeral string to convert.
   * @returns {number} The integer value of the Roman numeral.
   * @throws {Error} Throws an error if the input is not a valid or canonical Roman numeral.
   */
  function romanToInteger(roman) {
    // Validate that the input is a non-empty string.
    if (typeof roman !== 'string' || roman.trim() === '') {
      throw new Error('Input must be a valid Roman numeral.');
    }

    // Standardize the input by converting it to uppercase.
    const standardizedRoman = roman.toUpperCase();

    // Check that the string contains only valid Roman numeral characters.
    if (!/^[IVXLCDM]+$/.test(standardizedRoman)) {
      throw new Error('The Roman numeral contains invalid characters.');
    }

    let total = 0;
    let previousValue = 0;

    // Iterate through the numeral from right to left to handle subtractive notation easily.
    for (let i = standardizedRoman.length - 1; i >= 0; i--) {
      const currentValue = ROMAN_CHARACTER_VALUES[standardizedRoman[i]];
      if (currentValue < previousValue) {
        // If the current numeral is less than the previous numeral, subtract its value.
        total -= currentValue;
      } else {
        // Otherwise, add its value.
        total += currentValue;
      }
      previousValue = currentValue;
    }

    // Validate that the Roman numeral is in canonical form.
    // This is done by converting the computed integer back to a Roman numeral
    // and comparing it with the standardized input.
    const reconversion = integerToRoman(total);
    if (reconversion !== standardizedRoman) {
      throw new Error('The Roman numeral is not in canonical form.');
    }

    return total;
  }

  // Expose the public conversion methods
  return {
    integerToRoman,
    romanToInteger
  };
})();

// Export for Node.js environments (e.g. testing)
if (typeof module !== 'undefined' && module.exports) {
  module.exports = RomanConverter;
}
