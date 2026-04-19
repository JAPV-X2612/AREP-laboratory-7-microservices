import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Fetches the global public post stream. No authentication required.
 *
 * @returns {Promise<Array>} array of post objects
 */
export async function fetchStream() {
  const { data } = await axios.get(`${BASE_URL}/api/stream`);
  return Array.isArray(data) ? data : (data.posts || []);
}

/**
 * Creates a new post using the provided Auth0 access token.
 *
 * @param {string} content     post body (max 140 characters)
 * @param {string} accessToken Auth0 Bearer access token
 * @returns {Promise<Object>} the created post object
 */
export async function createPost(content, accessToken) {
  const { data } = await axios.post(
    `${BASE_URL}/api/posts`,
    { content },
    { headers: { Authorization: `Bearer ${accessToken}` } }
  );
  return data;
}

/**
 * Fetches the authenticated user's profile using the provided Auth0 access token.
 *
 * @param {string} accessToken Auth0 Bearer access token
 * @returns {Promise<Object>} the user profile object
 */
export async function fetchMe(accessToken) {
  const { data } = await axios.get(`${BASE_URL}/api/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return data;
}
