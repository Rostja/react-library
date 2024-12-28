export const oktaConfig = {
    clientId: '0oam5nhgayhFJUi5M5d7' ,
    issuer:'https://dev-54604687.okta.com/oauth2/default',
    redirectUrl: 'http://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,  // scopes requested by your app
}