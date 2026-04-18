import React, { useEffect, useState } from 'react';
import PostCard from '../post/PostCard';
import { fetchStream } from '../../services/apiService';

/**
 * Fetches and renders the global public post stream.
 * Prepends a newly created post when the newPost prop changes.
 *
 * @param {Object|null} newPost a newly created post to prepend to the feed
 */
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

  if (loading) return <p>Loading stream...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (posts.length === 0) return <p>No posts yet. Be the first!</p>;

  return (
    <div>
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}

export default StreamFeed;
