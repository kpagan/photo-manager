export type AppConfig = {
  appName: string;
  version: string;
  backendStatus: string;
  description: string;
  features: string[];
};

export type ScanResponse = {
  message: string;
  jobId?: string;
  status?: string;
};

const DEFAULT_API_BASE_URL = 'http://localhost:8080/api';

async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const baseUrl = import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL;
  const response = await fetch(`${baseUrl}${path}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...init,
  });

  if (!response.ok) {
    throw new Error(`Request failed with ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export async function fetchAppConfig(): Promise<AppConfig> {
  return requestJson<AppConfig>('/config');
}

export async function startFolderScan(): Promise<ScanResponse> {
  return requestJson<ScanResponse>('/folder-scan/start', {
    method: 'POST',
  });
}

export { DEFAULT_API_BASE_URL };
