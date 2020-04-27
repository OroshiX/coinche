export function isNotNullAndNotUndefined(obj: any): boolean {
  return obj !== undefined && obj !== null;
}

export function enumToMap(enumeration: any): Map<string, string | number> {
  const map = new Map<string, string | number>();
  for (const key in enumeration) {
    // TypeScript does not allow enum keys to be numeric
    if (!isNaN(Number(key))) continue;

    const val = enumeration[key] as string | number;

    // TypeScript does not allow enum value to be null or undefined
    if (val !== undefined && val !== null)
      map.set(key, val);
  }
  console.log(JSON.stringify(map));
  return map;
}

export function enumToObjList(enumInput: any) {
  const map = enumToMap(enumInput);
  return Array.from(map.entries()).map((k, v )=> ({id: k, value: v}));
}

export function enumToEntries(enumInput: any) {
  const map = enumToMap(enumInput);
  return Array.from(map.entries());
}

export function enumToKeys(enumInput: any) {
  const map = enumToMap(enumInput);
  return Array.from(map.keys());
}

export function enumToValues(enumInput: any) {
  const map = enumToMap(enumInput);
  return Array.from(map.values());
}
