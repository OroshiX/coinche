import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'shorterName'
})
export class ShorterNamePipe implements PipeTransform {

  transform(value: string, limit: number): string {
    if (!!value && value.length > 0) {
      const arr = value.split('-');
      return value.length <= limit ? value : arr.length > 0 ?
        arr[0].charAt(0) + '-' + arr[1].substr(0, limit - 1) :
        value.substr(0, limit);
    } else {
      return value;
    }
  }

}
