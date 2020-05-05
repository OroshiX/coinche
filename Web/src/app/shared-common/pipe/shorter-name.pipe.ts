import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'shorterName'
})
export class ShorterNamePipe implements PipeTransform {

  transform(value: string, limit: number): string {
    if (!!value && value.length <= limit) {
      return value;
    } else if (!!value && value.length > limit) {
      const arrHyphen = value.split('-');
      const arrSpace = value.split(' ');
      const arrUnderS = value.split('_');
      if (arrHyphen.length > 1) {
        const valShortened = arrHyphen[0].charAt(0) + '-' + arrHyphen[1];
        return valShortened.slice(0, limit);
      } else if (arrSpace.length > 1) {
        const valS = arrSpace[0].trim().charAt(0) + ' ' + arrSpace[1].trim();
        return valS.slice(0, limit);
      } else if (arrUnderS.length > 1) {
        const valS = arrUnderS[0].trim().charAt(0) + '_' + arrUnderS[1].trim();
        return valS.slice(0, limit);
      } else {
        return value.slice(0, limit);
      }
    } else {
      return '';
    }
  }

}
