import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import LogoutButton from '../components/auth/LogoutButton';
import PostForm from '../components/post/PostForm';
import StreamFeed from '../components/stream/StreamFeed';

/**
 * Main page shown to authenticated users.
 * Displays the post composition form, the user's nickname, and the global stream.
 */
function HomePage() {
  const { user } = useAuth0();
  const [newPost, setNewPost] = useState(null);

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: '1rem' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Home</h1>
        <div>
          <span style={{ marginRight: '1rem' }}>@{user?.nickname}</span>
          <LogoutButton />
        </div>
      </header>
      <PostForm onPostCreated={setNewPost} />
      <hr />
      <StreamFeed newPost={newPost} />
    </div>
  );
}

export default HomePage;
