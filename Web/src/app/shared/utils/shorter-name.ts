export function shortenStr(str: string, limit: number): string {
  const re = new RegExp('\\s+|-|_|\\.');
  const arr = str?.split(re).map(w => w.charAt(0).toUpperCase() + w.substr(1).toLowerCase());
  if (!!arr && arr.length > 1 && limit > 2) {
    const valShortened = arr[0].charAt(0) + ' ' + arr[1];
    return valShortened.substr(0, limit);
  } else if (!!arr && arr.length > 0 && limit > 2) {
    return arr[0].substr(0, limit);
  } else {
    return '';
  }
}
