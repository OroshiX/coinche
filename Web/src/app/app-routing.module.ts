import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGoogleComponent } from './login-google/login-google.component';
import { CanActivateGame } from './services/can-activates/can-activate-game';


const routes: Routes = [
  {
    path: 'login',
    component: LoginGoogleComponent
  },
  {
    path: 'play',
    loadChildren: () => import('./games/game/game.module').then(m => m.GameModule),
    canActivate: [CanActivateGame]
  },
  {
    path: 'all-games',
    loadChildren: () => import('./lobby/all-games/all-games.module').then(m => m.AllGamesModule),
    canActivate: [CanActivateGame]
  },
  { path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: '**',
    component: LoginGoogleComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
