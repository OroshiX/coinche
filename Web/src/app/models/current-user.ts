export class UserToken {
  id: string;
  email: string;
  password: string;
  constructor(email: string, password: string) {
    this.email = name;
    this.password = password;
  }
}

export class Permissions {
  canActivate(user: UserToken): boolean {
    return true;
  }
}
