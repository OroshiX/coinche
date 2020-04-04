import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGoogleComponent } from './login-google/login-google.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { CanActivateGame } from './services/can-activate-game';


const routes: Routes = [
  {
    path: 'login',
    component: LoginGoogleComponent
  },
  {
    path: 'play',
    loadChildren: () => import('./game/game.module').then(m => m.GameModule),
    canActivate: [CanActivateGame]
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
