import { Card, CardView, KeyValue } from '../models/play';

export function isNotNullAndNotUndefined(obj: any): boolean {
  return obj !== undefined && obj !== null;
}

export function enumToMap(enumeration: any): Map<string, any> {
  const map = new Map<string, any>();
  for (const key in enumeration) {
    // TypeScript does not allow enum keys to be numeric
    if (!isNaN(Number(key))) {
      continue;
    }

    const val = enumeration[key];

    // TypeScript does not allow enum value to be null or undefined
    if (val !== undefined && val !== null) {
      map.set(key, val);
    }
  }
  return map;
}

export function enumToObjList(enumInput: any) {
  const map = enumToMap(enumInput);
  return Array.from(map.entries()).map((k, v) => ({id: k, value: v}));
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

export function mapToArrayObj(map: Map<string, CardView>, playable: boolean): KeyValue[] {
  return Array.from(map.entries())
    .map(([k, v]) => ({id: k, value: v}))
    .filter((obj) => obj.value.playable === playable);
}

export function arrayObjToMap(list: KeyValue[]): Map<string, CardView> {
  const map = new Map<string, CardView>();
  list.map((obj => map.set(obj.id, obj.value)));
  return map;
}

export function rebuildOrderedMap(mapOrig: Map<string, CardView>): Map<string, CardView> {
  const arrayPlayableNull = mapToArrayObj(mapOrig, null);
  if (arrayPlayableNull.length > 0) {
    return mapOrig;
  }
  const arrayPlayableTrue = mapToArrayObj(mapOrig, true);
  const arrayPlayableFalse = mapToArrayObj(mapOrig, false);
  const concatList = [...arrayPlayableTrue, ...arrayPlayableFalse];
  return arrayObjToMap(concatList);
}

export function buildOrderedListPlayableNull(cards: Card[]): Card[] {
  return cards.filter(card => card.playable === null);
}

export function buildOrderedListPlayableTrueAndFalse(cards: Card[]): Card[] {
  const listNull = cards.filter(card => card.playable === null);
  if (listNull.length > 0) {
    return listNull;
  }
  const listTrue =  cards.filter(card => card.playable === true);
  const listFalse =cards.filter(card => card.playable === false);
  return [...listFalse, ...listTrue];
}
