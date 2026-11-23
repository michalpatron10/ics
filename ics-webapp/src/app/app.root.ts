import {Component, inject} from '@angular/core';
import {OAuthService} from 'angular-oauth2-oidc';

@Component({
  imports: [],
  selector: 'app-root',
  styleUrl: './app.root.css',
  templateUrl: './app.root.html',
})
export class AppRoot {

  private oauthService = inject(OAuthService);

  protected logout() {
    this.oauthService.logOut();
  }
}
