import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import LogoutButton from '../components/auth/LogoutButton';
import PostForm from '../components/post/PostForm';
import StreamFeed from '../components/stream/StreamFeed';

function HomePage() {
  const { user } = useAuth0();
  const [newPost, setNewPost] = useState(null);
  const nickname = user?.nickname || user?.name || user?.email?.split('@')[0] || 'user';

  return (
    <div className="app-shell">
      <header className="topbar">
        <h1>Home</h1>
        <div className="user-chip">
          <span className="handle">@{nickname}</span>
          <LogoutButton />
        </div>
      </header>
      <PostForm onPostCreated={setNewPost} />
      <StreamFeed newPost={newPost} />
    </div>
  );
}

export default HomePage;
