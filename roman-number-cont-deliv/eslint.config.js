// eslint.config.js
module.exports = [
  {
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        window: 'readonly',
        document: 'readonly',
        gtag: 'readonly',
        console: 'readonly',
        describe: 'readonly',
        it: 'readonly',
        mocha: 'readonly',
        chai: 'readonly'
      }
    },
    rules: {
      'no-cond-assign': [
        'error',
        'always'
      ],
      'indent': [
        'error',
        2
      ],
      'linebreak-style': [
        'error',
        'unix'
      ],
      'quotes': [
        'error',
        'single'
      ],
      'no-unused-vars': 'off',
      'semi': [
        'error',
        'always'
      ]
    }
  }
];
