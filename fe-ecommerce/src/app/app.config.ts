import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  HttpClientModule,
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi,
  withJsonpSupport,
} from '@angular/common/http';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { jsonInterceptor } from './core/interceptors/json.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule),
    provideHttpClient(
      withInterceptors([jwtInterceptor, jsonInterceptor]), 
    ),
    provideAnimationsAsync(),
  ],
};
