import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import HomePage from './pages/HomePage';
import LoginButton from './components/auth/LoginButton';

/**
 * Root application component. Renders the login screen for unauthenticated
 * users and the main feed for authenticated users.
 */
function App() {
  const { isLoading, isAuthenticated, error } = useAuth0();

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Authentication error: {error.message}</p>;

  return isAuthenticated ? <HomePage /> : (
    <div style={{ textAlign: 'center', marginTop: '4rem' }}>
      <h1>Twitter-like App</h1>
      <p>Sign in to post and join the conversation.</p>
      <LoginButton />
    </div>
  );
}

export default App;
