import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const rolesAutorises = route.data['roles'] as string[];
  const roleUtilisateur = authService.getRole();

  if (roleUtilisateur && rolesAutorises.includes(roleUtilisateur)) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
