import { Pipe, PipeTransform } from '@angular/core';
import { shortenStr } from '../../shared/utils/shorter-name';

@Pipe({
  name: 'shorterName'
})
export class ShorterNamePipe implements PipeTransform {
  transform(str: string, limit: number): string {
    return shortenStr(str, limit);
  }
}


