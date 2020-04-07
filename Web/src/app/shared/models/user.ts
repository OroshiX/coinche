export class CurrentUser {
  uid: any;
  email: string;
  displayName: string;
  idToken: any;
  photoURL: string;
  emailVerified: boolean;

  constructor(obj: Partial<CurrentUser>) {
    Object.assign(this, obj);
  }
}
