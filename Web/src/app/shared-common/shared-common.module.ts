import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ShorterNamePipe } from './pipe/shorter-name.pipe';


@NgModule({
  declarations: [ShorterNamePipe],
  exports: [
    ShorterNamePipe
  ],
  imports: [
    CommonModule
  ]
})
export class SharedCommonModule { }
