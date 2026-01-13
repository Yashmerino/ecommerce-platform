module.exports = {
  preset: 'ts-jest',
  testEnvironment: "jsdom",
  roots: ["<rootDir>/src"],
  testMatch: [
    "**/__tests__/**/*.+(ts|tsx|js)",
    "**/?(*.)+(spec|test).+(ts|tsx|js)",
  ],
  globals: {
    'ts-jest': {
      useESM: true,
      tsconfig: {
        jsx: 'react-jsx',
        module: 'esnext',
      },
    },
  },
  transform: {
    "^.+\\.(ts|tsx)$": ["ts-jest", {
      useESM: true,
      tsconfig: {
        jsx: 'react-jsx',
        module: 'esnext',
      },
    }],
  },
  extensionsToTreatAsEsm: ['.ts', '.tsx'],
  automock: false,
  setupFilesAfterEnv: ["./setupTests.ts"],
  moduleNameMapper: {
    "^.+\\.(jpg|jpeg|png|gif|xml|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$":
      "<rootDir>/fileMock.js",
  },
};
