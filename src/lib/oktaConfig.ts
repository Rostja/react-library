export const oktaConfig = {
    clientId: '0oalxx38yizx5eNtt5d7' ,
    issuer:'https://dev-58820038.okta.com/oauth2/default',
    redirectUrl: 'http://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,  // scopes requested by your app
}