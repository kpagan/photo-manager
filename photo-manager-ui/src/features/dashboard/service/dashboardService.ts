import { requestJson } from '../../../services/appService';
import type { DashboardDto } from '../model/DashboardDto';

export async function fetchDashboardData() {
  return requestJson<DashboardDto>('/dashboard');
}