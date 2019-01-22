package org.dice_research.squirrel.data.uri.norm;

import java.net.URI;

/**
 * Path normalization adapted from the {@link URI} class (which is based upon
 * src/solaris/native/java/io/canonicalize_md.c). The algorithm for path
 * normalization avoids the creation of a string object for each segment, as
 * well as the use of a string buffer to compute the final result, by using a
 * single char array and editing it in place. The array is first split into
 * segments, replacing each slash with {@code '\0'} and creating a segment-index
 * array, each element of which is the index of the first char in the
 * corresponding segment. We then walk through both arrays, removing
 * {@code "."}, {@code ".."}, and other segments as necessary by setting their
 * entries in the index array to -1. Finally, the two arrays are used to rejoin
 * the segments and compute the final result.
 */
public class PathNormalization {

    /**
     * <p>
     * Check the given path to see if it might need normalization. A path might need
     * normalization if it contains duplicate slashes, a "." segment, or a ".."
     * segment. Return -1 if no further normalization is possible, otherwise return
     * the number of segments found.
     * </p>
     * 
     * <p>
     * This method takes a string argument rather than a char array so that this
     * test can be performed without invoking path.toCharArray().
     * </p>
     * 
     * @param path
     *            the path that should be checked
     * @return the number of segments that can be normalized or -1 if no further
     *         normalization is possible
     */
    protected static int needsNormalization(String path) {
        boolean normal = true;
        int ns = 0; // Number of segments
        int end = path.length() - 1; // Index of last char in path
        int p = 0; // Index of next char in path

        // Skip initial slashes
        while (p <= end) {
            if (path.charAt(p) != '/')
                break;
            p++;
        }
        if (p > 1)
            normal = false;

        // Scan segments
        while (p <= end) {

            // Looking at "." or ".." ?
            if ((path.charAt(p) == '.') && ((p == end) || ((path.charAt(p + 1) == '/')
                    || ((path.charAt(p + 1) == '.') && ((p + 1 == end) || (path.charAt(p + 2) == '/')))))) {
                normal = false;
            }
            ns++;

            // Find beginning of next segment
            while (p <= end) {
                if (path.charAt(p++) != '/')
                    continue;

                // Skip redundant slashes
                while (p <= end) {
                    if (path.charAt(p) != '/')
                        break;
                    normal = false;
                    p++;
                }

                break;
            }
        }

        return normal ? -1 : ns;
    }

    /**
     * <p>
     * Split the given path into segments, replacing slashes with nulls and filling
     * in the given segment-index array.
     * 
     * <p>
     * Preconditions:<br>
     * {@code segs.length ==} Number of segments in path
     * </p>
     * 
     * <p>
     * Postconditions:<br>
     * All slashes in path replaced by {@code '\0'}<br>
     * {@code segs[i] ==} Index of first char in segment i
     * {@code (0 <= i < segs.length)}
     * </p>
     * 
     * @param path
     *            that path that should be split
     * @param segs
     *            the positions of the segments
     */
    protected static void split(char[] path, int[] segs) {
        int end = path.length - 1; // Index of last char in path
        int p = 0; // Index of next char in path
        int i = 0; // Index of current segment

        // Skip initial slashes
        while (p <= end) {
            if (path[p] != '/')
                break;
            path[p] = '\0';
            p++;
        }

        while (p <= end) {

            // Note start of segment
            segs[i++] = p++;

            // Find beginning of next segment
            while (p <= end) {
                if (path[p++] != '/')
                    continue;
                path[p - 1] = '\0';

                // Skip redundant slashes
                while (p <= end) {
                    if (path[p] != '/')
                        break;
                    path[p++] = '\0';
                }
                break;
            }
        }

        if (i != segs.length)
            throw new InternalError(); // ASSERT
    }

    //
    /**
     * Join the segments in the given path according to the given segment-index
     * array, ignoring those segments whose index entries have been set to -1, and
     * inserting slashes as needed. Return the length of the resulting path.
     * <p>
     * Preconditions:<br>
     * {@code segs[i] == -1} implies segment i is to be ignored<br>
     * path computed by split, as above, with {@code '\0'} having replaced
     * {@code '/'}
     * </p>
     * <p>
     * Postconditions:<br>
     * {@code path[0] .. path[return value]} == Resulting path
     * </p>
     * 
     * @param path
     *            the path as char array
     * @param segs
     *            the positions of the segments
     * @return the length of the new path in the path array
     */
    protected static int join(char[] path, int[] segs) {
        int ns = segs.length; // Number of segments
        int end = path.length - 1; // Index of last char in path
        int p = 0; // Index of next path char to write

        if (path[p] == '\0') {
            // Restore initial slash for absolute paths
            path[p++] = '/';
        }

        for (int i = 0; i < ns; i++) {
            int q = segs[i]; // Current segment
            if (q == -1)
                // Ignore this segment
                continue;

            if (p == q) {
                // We're already at this segment, so just skip to its end
                while ((p <= end) && (path[p] != '\0'))
                    p++;
                if (p <= end) {
                    // Preserve trailing slash
                    path[p++] = '/';
                }
            } else if (p < q) {
                // Copy q down to p
                while ((q <= end) && (path[q] != '\0'))
                    path[p++] = path[q++];
                if (q <= end) {
                    // Preserve trailing slash
                    path[p++] = '/';
                }
            } else
                throw new InternalError(); // ASSERT false
        }

        return p;
    }

    /**
     * Remove "." segments from the given path, and remove segment pairs consisting
     * of a non-".." segment followed by a ".." segment.
     * 
     * @param path
     *            the path as char array
     * @param segs
     *            the positions of the segments
     */
    protected static void removeDots(char[] path, int[] segs) {
        int ns = segs.length;
        int end = path.length - 1;

        for (int i = 0; i < ns; i++) {
            int dots = 0; // Number of dots found (0, 1, or 2)

            // Find next occurrence of "." or ".."
            do {
                int p = segs[i];
                if (path[p] == '.') {
                    if (p == end) {
                        dots = 1;
                        break;
                    } else if (path[p + 1] == '\0') {
                        dots = 1;
                        break;
                    } else if ((path[p + 1] == '.') && ((p + 1 == end) || (path[p + 2] == '\0'))) {
                        dots = 2;
                        break;
                    }
                }
                i++;
            } while (i < ns);
            if ((i > ns) || (dots == 0))
                break;

            if (dots == 1) {
                // Remove this occurrence of "."
                segs[i] = -1;
            } else {
                // If there is a preceding non-".." segment, remove both that
                // segment and this occurrence of ".."; otherwise, leave this
                // ".." segment as-is.
                int j;
                for (j = i - 1; j >= 0; j--) {
                    if (segs[j] != -1)
                        break;
                }
                if (j >= 0) {
                    int q = segs[j];
                    if (!((path[q] == '.') && (path[q + 1] == '.') && (path[q + 2] == '\0'))) {
                        segs[i] = -1;
                        segs[j] = -1;
                    }
                }
            }
        }
    }

    /**
     * Handles a special case: If the normalized path is relative, and if the first
     * segment could be parsed as a scheme name, then prepend a "." segment
     * 
     * @param path
     *            the path as char array
     * @param segs
     *            the positions of the segments
     */
    protected static void maybeAddLeadingDot(char[] path, int[] segs) {

        if (path[0] == '\0')
            // The path is absolute
            return;

        int ns = segs.length;
        int f = 0; // Index of first segment
        while (f < ns) {
            if (segs[f] >= 0)
                break;
            f++;
        }
        if ((f >= ns) || (f == 0))
            // The path is empty, or else the original first segment survived,
            // in which case we already know that no leading "." is needed
            return;

        int p = segs[f];
        while ((p < path.length) && (path[p] != ':') && (path[p] != '\0'))
            p++;
        if (p >= path.length || path[p] == '\0')
            // No colon in first segment, so no "." needed
            return;

        // At this point we know that the first segment is unused,
        // hence we can insert a "." segment at that position
        path[0] = '.';
        path[1] = '\0';
        segs[0] = 0;
    }

    /**
     * Normalize the given path string. A normal path string has no empty segments
     * (i.e., occurrences of "//"), no segments equal to ".", and no segments equal
     * to ".." that are preceded by a segment not equal to "..". In contrast to
     * Unix-style pathname normalization, for URI paths we always retain trailing
     * slashes.
     * 
     * @param ps
     *            the path that should be normalized
     * @return the new path or exactly the same path object if no changes were made
     */
    public static String normalize(String ps) {

        // Does this path need normalization?
        int ns = needsNormalization(ps); // Number of segments
        if (ns < 0)
            // Nope -- just return it
            return ps;

        char[] path = ps.toCharArray(); // Path in char-array form

        // Split path into segments
        int[] segs = new int[ns]; // Segment-index array
        split(path, segs);

        // Remove dots
        removeDots(path, segs);

        // Prevent scheme-name confusion
        maybeAddLeadingDot(path, segs);

        // Join the remaining segments and return the result
        String s = new String(path, 0, join(path, segs));
        if (s.equals(ps)) {
            // string was already normalized
            return ps;
        }
        return s;
    }
}
