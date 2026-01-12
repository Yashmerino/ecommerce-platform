/**
 * Type for .jpg files.
 */
declare module '*.jpg' {
    const value: any;
    export = value;
}

/**
 * Type for .png files.
 */
declare module '*.png' {
  const value: string;
  export default value;
}