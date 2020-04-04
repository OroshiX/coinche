export class User {
  uid: any;
  email: string;
  displayName: string;
  idToken: any;
  photoURL: string;
  emailVerified: boolean;

  constructor(obj: Partial<User>) {
    Object.assign(this, obj);
  }
}
