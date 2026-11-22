import {ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {OAuthService, provideOAuthClient} from 'angular-oauth2-oidc';
import {provideHttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';

function initializeOAuth(oauthService: OAuthService): Promise<void> {
  return new Promise((resolve, reject) => {
    oauthService.configure(environment.authConfig);
    oauthService.setupAutomaticSilentRefresh();
    oauthService.loadDiscoveryDocumentAndLogin()
      .then((isLoggedIn) => {
        if (isLoggedIn) {
          resolve();
        }
      })
      .catch((err) => {
        window.location.href = '/error.html';
        reject(err);
      });
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    provideOAuthClient(),
    provideAppInitializer(() => {
      const oauthService = inject(OAuthService);
      return initializeOAuth(oauthService);
    })
  ]
};
