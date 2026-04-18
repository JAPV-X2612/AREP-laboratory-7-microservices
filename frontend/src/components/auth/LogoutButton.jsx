import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';

/**
 * Button that triggers the Auth0 logout and redirects to the app origin.
 */
function LogoutButton() {
  const { logout } = useAuth0();
  return (
    <button onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}>
      Log Out
    </button>
  );
}

export default LogoutButton;
