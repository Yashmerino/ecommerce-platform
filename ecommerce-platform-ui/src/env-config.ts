// Handle both Vite (import.meta) and Jest (process.env) environments
const getEnv = (key: string, defaultValue: string): string => {
  // For Vite/Browser environment using import.meta
  // Use globalThis to avoid TypeScript compilation issues in Jest
  if (typeof globalThis !== 'undefined' && (globalThis as any).import?.meta?.env?.[key]) {
    return (globalThis as any).import.meta.env[key];
  }
  
  // Try the standard way for Vite
  try {
    const meta = (window as any).__vite_import_meta || {};
    if (meta.env?.[key]) {
      return meta.env[key];
    }
  } catch {
    // Not in browser environment
  }
  
  // For Jest/Test environment using process.env
  if (typeof process !== 'undefined' && process.env?.[key]) {
    return process.env[key]!;
  }
  
  return defaultValue;
};

export const API_BASE_URL = getEnv('VITE_API_BASE_URL', 'http://localhost:8081');
export const STRIPE_PUBLISHABLE_KEY = getEnv('VITE_STRIPE_PUBLISHABLE_KEY', 'pk_test_51Sp6v1KAi7W8OloWy4X1iKCE8ORDVdvoenOB8KlwZUQ4rBPmAwx5Opk9lFfbJW8g1qE4hq0YcWeyjgyBo59pCK1l00fMjzcgJX');