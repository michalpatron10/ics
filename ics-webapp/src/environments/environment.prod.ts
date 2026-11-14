import { AuthConfig } from 'angular-oauth2-oidc';

export const environment = {
  production: true,
  authConfig: {
    issuer: 'https://auth.yourdomain.com/realms/ics-realm',
    tokenEndpoint: 'https://auth.yourdomain.com/realms/ics-realm/protocol/openid-connect/token',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : '',
    clientId: 'ics-webapp-client',
    responseType: 'code',
    scope: 'openid profile',
    showDebugInformation: false,
  } as AuthConfig,
};
