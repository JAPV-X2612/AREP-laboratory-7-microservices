import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';

/**
 * Button that triggers the Auth0 login redirect flow.
 */
function LoginButton() {
  const { loginWithRedirect } = useAuth0();
  return <button onClick={() => loginWithRedirect()}>Log In</button>;
}

export default LoginButton;
