import { request, requestJson } from '../../../services/appService';
import type { DashboardDto } from '../model/DashboardDto';
import type { ScanStatusDto } from '../model/ScanStatusDto';

export async function fetchDashboardData() {
  return requestJson<DashboardDto>('/dashboard');
}

export async function startFolderScan() : Promise<Response> {
  return request('/scan', {
    method: 'POST'
  });
}

export async function fetchScanStatus() : Promise<ScanStatusDto> {
  return requestJson<ScanStatusDto>('/scanStatus');
}
