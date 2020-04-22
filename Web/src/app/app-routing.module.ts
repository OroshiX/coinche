import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGoogleComponent } from './login-google/login-google.component';
import { GameGuard } from './services/guards/game.guard';

const routes: Routes = [
  {
    path: 'login',
    component: LoginGoogleComponent
  },
  {
    path: 'all-games',
    loadChildren: () => import('./lobby/all-games/all-games.module').then(m => m.AllGamesModule),

  },
  {
    path: 'play/:id',
    loadChildren: () => import('./games/game/game.module').then(m => m.GameModule),
    canActivate: [GameGuard]
  },
  {
    path: 'distributing',
    loadChildren: () => import('./games/distributing/distributing.module').then(m => m.DistributingModule),
    canActivate: [GameGuard]
  },
  {
    path: 'grid',
    loadChildren: () => import('./grid/grid/grid.module').then(m => m.GridModule),
  },
  {
    path: '**',
    component: LoginGoogleComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {enableTracing: false}  // <-- debugging purpose only
  )],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
