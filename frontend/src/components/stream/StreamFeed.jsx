import React, { useEffect, useState } from 'react';
import PostCard from '../post/PostCard';
import { fetchStream } from '../../services/apiService';

function StreamFeed({ newPost }) {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchStream()
      .then(setPosts)
      .catch(() => setError('Failed to load stream.'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (newPost) {
      setPosts((prev) => [newPost, ...prev]);
    }
  }, [newPost]);

  if (loading) return <div className="feed-state">Loading stream...</div>;
  if (error) return <div className="error-banner">{error}</div>;
  if (posts.length === 0) return <div className="feed-state">No posts yet. Be the first!</div>;

  return (
    <div>
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}

export default StreamFeed;
