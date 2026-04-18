import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { fetchMe } from '../services/apiService';

/**
 * Profile page that fetches and displays the authenticated user's profile from GET /api/me.
 */
function ProfilePage() {
  const { getAccessTokenSilently } = useAuth0();
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    getAccessTokenSilently({
      authorizationParams: { audience: import.meta.env.VITE_AUTH0_AUDIENCE },
    })
      .then(fetchMe)
      .then(setProfile)
      .catch(() => setError('Failed to load profile.'));
  }, [getAccessTokenSilently]);

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!profile) return <p>Loading profile...</p>;

  return (
    <div>
      <h2>Profile</h2>
      <p><strong>Nickname:</strong> {profile.nickname}</p>
      <p><strong>Email:</strong> {profile.email}</p>
      <p><strong>Member since:</strong> {new Date(profile.createdAt).toLocaleDateString()}</p>
    </div>
  );
}

export default ProfilePage;
