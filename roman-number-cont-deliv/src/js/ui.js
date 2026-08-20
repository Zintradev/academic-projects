/**
 * UI controller module.
 * Wraps UI event handlers and DOM bindings.
 */
(function() {
  // Destructure public methods from the converter namespace
  const { integerToRoman, romanToInteger } = RomanConverter;

  /**
   * Handles the conversion process when the user clicks the convert button.
   * Reads the inputs from the DOM, executes the appropriate conversion function,
   * and updates the DOM to display the result or error message. Also triggers
   * Google Analytics event tracking if available.
   */
  function handleConversion() {
    // Retrieve the selected conversion mode (either 'intToRoman' or 'romanToInt').
    const mode = document.getElementById('conversionMode').value;
    // Get the user input from the input field.
    const input = document.getElementById('inputValue').value.trim();
    // Get references to the result and error display elements.
    const resultDiv = document.getElementById('result');
    const errorDiv = document.getElementById('error');

    // Clear any previous result or error messages.
    resultDiv.textContent = '';
    errorDiv.textContent = '';

    try {
      if (mode === 'intToRoman') {
        // Attempt to parse the input as an integer.
        const num = parseInt(input, 10);
        if (isNaN(num)) {
          throw new Error('Please enter a valid integer number.');
        }
        // Convert the integer to a Roman numeral.
        const roman = integerToRoman(num);
        resultDiv.textContent = `Roman Numeral: ${roman}`;
      } else if (mode === 'romanToInt') {
        // Convert the Roman numeral to an integer.
        const num = romanToInteger(input);
        resultDiv.textContent = `Integer: ${num}`;
      }

      // Google Analytics event tracking
      if (typeof gtag === 'function') {
        gtag('event', 'conversion_success', {
          'conversion_mode': mode,
          'input_length': input.length
        });
      }
    } catch (error) {
      // Display any error messages encountered during conversion.
      errorDiv.textContent = error.message;

      // Google Analytics event tracking for failure
      if (typeof gtag === 'function') {
        gtag('event', 'conversion_error', {
          'conversion_mode': mode,
          'error_message': error.message
        });
      }
    }
  }

  // Safely attach event listener only if the DOM elements exist (e.g. not in browser tests)
  if (typeof document !== 'undefined') {
    const convertBtn = document.getElementById('convertButton');
    if (convertBtn) {
      convertBtn.addEventListener('click', handleConversion);
    }
  }
})();
