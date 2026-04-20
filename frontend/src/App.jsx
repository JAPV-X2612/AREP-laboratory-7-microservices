import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import HomePage from './pages/HomePage';
import LoginButton from './components/auth/LoginButton';

function App() {
  const { isLoading, isAuthenticated, error } = useAuth0();

  if (isLoading) {
    return <div className="feed-state">Loading...</div>;
  }
  if (error) {
    return <div className="error-banner">Authentication error: {error.message}</div>;
  }

  if (isAuthenticated) {
    return <HomePage />;
  }

  return (
    <div className="login-screen">
      <div className="logo">🐦</div>
      <h1>Twitter-like App</h1>
      <p>Sign in to post and join the conversation. Microservices on AWS Lambda + DynamoDB, secured with Auth0.</p>
      <LoginButton />
    </div>
  );
}

export default App;
