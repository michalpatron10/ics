import { AuthConfig } from 'angular-oauth2-oidc';

export const environment = {
  production: false,
  authConfig: {
    issuer: 'http://localhost:8180/realms/ics-realm',
    tokenEndpoint: 'http://localhost:8180/realms/ics-realm/protocol/openid-connect/token',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : '',
    clientId: 'ics-webapp-client',
    responseType: 'code',
    scope: 'openid profile',
    showDebugInformation: true,
  } as AuthConfig,
};
