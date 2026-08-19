export type Role = 'CLIENT' | 'AGENT_CREDIT' | 'MANAGER';

export interface AuthResponse {
  token: string;
  email: string;
  role: Role;
}
