export type LoanType = 'IMMOBILIER' | 'AUTO' | 'PERSONNEL';
export type LoanStatus = 'SOUMISE' | 'EN_ANALYSE' | 'APPROUVEE' | 'REJETEE' | 'DECAISSEE';
export type EmploymentType = 'CDI' | 'CDD' | 'INDEPENDANT' | 'SANS_EMPLOI';

export interface LoanApplicationRequest {
  typePret: LoanType;
  montantDemande: number;
  dureeMois: number;
  tauxInteret: number;
  revenuMensuel: number;
  chargesMensuelles: number;
  situationProfessionnelle: EmploymentType;
  ancienneteMois: number;
  apportPersonnel: number;
}

export interface LoanApplicationResponse {
  id: number;
  typePret: LoanType;
  montantDemande: number;
  statut: LoanStatus;
  scoreGlobal: number;
  detailFacteurs: string;
  dateSoumission: string;
}
