import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { createPost } from '../../services/apiService';

/**
 * Form component for composing and submitting a new post.
 *
 * @param {Function} onPostCreated callback invoked with the new post after successful creation
 */
function PostForm({ onPostCreated }) {
  const { getAccessTokenSilently } = useAuth0();
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const remaining = 140 - content.length;

  async function handleSubmit(e) {
    e.preventDefault();
    if (!content.trim()) return;
    setLoading(true);
    setError('');
    try {
      const token = await getAccessTokenSilently({
        authorizationParams: { audience: import.meta.env.VITE_AUTH0_AUDIENCE },
      });
      const post = await createPost(content, token);
      setContent('');
      onPostCreated(post);
    } catch (err) {
      setError('Failed to create post. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={140}
        placeholder="What's happening?"
        rows={3}
        style={{ width: '100%', resize: 'vertical' }}
      />
      <div>
        <span style={{ color: remaining < 20 ? 'red' : 'inherit' }}>{remaining}</span>
        <button type="submit" disabled={loading || !content.trim()}>
          {loading ? 'Posting...' : 'Post'}
        </button>
      </div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
}

export default PostForm;
