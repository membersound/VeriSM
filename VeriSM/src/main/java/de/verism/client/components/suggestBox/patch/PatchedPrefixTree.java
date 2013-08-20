package de.verism.client.components.suggestBox.patch;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptObject;

/** !! IMPORTANT !!
 *  This class is basically not modified regarding GWT 2.5.0 package protected {@link PrefixTree}.
 *  But as the patched class {@link PatchedMultiWordSuggestOracle} depends on this class,
 *  it has been patched here. Therefore all JSNI and JS method statements where changed from 
 *  "com.google.gwt.user.client.ui" to "de.verism.client.components.suggestBox.patch".
 *  
 *  If this class is moved or renamed, remember to also modify these strings in this class.
 *  Or use eclipse > 'Move' menu entry to let eclipse do the refactoring.
 *  You will not get any pre-compiler errors as JSNI methods are written as Java comments and are
 *  therefore not detectable neither by eclipse nor on compilation.
 */
class PatchedPrefixTree extends AbstractCollection<String> {

  /**
   * Iterates over the structure of a PrefixTree. No concurrency checks are
   * made. This Iterator will output anything new added to the tree if the new
   * entry is after the current position of the Iterator.
   *
   * This Iterator implementation is iterative and uses an internal stack object
   * to avoid call-stack limitations and invocation overhead.
   */
  private static class PrefixTreeIterator implements Iterator<String> {

    // Called from JSNI.
    private JavaScriptObject stack;

    /**
     * Constructor.
     *
     * @param tree The base of the PrefixTree to iterate over
     */
    public PrefixTreeIterator(PatchedPrefixTree tree) {
      init();
      addTree(tree, "");
    }

    public boolean hasNext() {
      // Have nextImpl peek at the next value that would be returned.
      return nextImpl(true) != null;
    }

    /**
     * {@inheritDoc} Wraps the native implementation with some sanity checking.
     */
    public String next() {
      final String toReturn = nextImpl(false);

      // A null response indicates that there was no data to be had.
      if (toReturn == null) {
        // Sanity check.
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements in the iterator");
        } else {
          throw new RuntimeException(
              "nextImpl() returned null, but hasNext says otherwise");
        }
      }

      return toReturn;
    }

    public void remove() {
      throw new UnsupportedOperationException("PrefixTree does not support "
          + "removal.  Use clear()");
    }

    /**
     * Add a frame to the work stack.
     *
     * <pre>
     *  frame := {suffixNames, subtrees, prefix, index}
     *  suffixNames := All suffixes in the target PrefixTree
     *  subtrees := All subtrees in the target PrefixTree
     *  prefix := A string that next() will prepend to output from the frame
     *  index := Stores which suffix was last output
     * </pre>
     *
     * @param tree The tree to add
     * @param prefix The prefix to prepend to values in tree
     */
    private native void addTree(PatchedPrefixTree tree, String prefix) /*-{
      var suffixes = [];
      for (var suffix in tree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suffixes) {
        // Ignore object properties that aren't colon-prepended keys
        if (suffix.indexOf(':') == 0) {
          suffixes.push(suffix);
        }
      }

      var frame = {
        suffixNames: suffixes,
        subtrees: tree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::subtrees,
        prefix: prefix,
        index: 0
      };

      var stack = this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree$PrefixTreeIterator::stack;
      stack.push(frame);
    }-*/;

    /**
     * Initialize JSNI objects.
     */
    private native void init() /*-{
      this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree$PrefixTreeIterator::stack = [];
    }-*/;

    /**
     * Access JSNI structures.
     *
     * @param peek If this is true, don't advance the iteration, just return the
     *          value that next() would return if it were called
     * @return The next object, or null if there is an error
     */
    private native String nextImpl(boolean peek) /*-{
      var stack = this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree$PrefixTreeIterator::stack;
      var safe = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::safe(Ljava/lang/String;)
      var unsafe = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)

      // Put this in a while loop to handle descent into subtrees without recursion.
      while (stack.length > 0) {
        var frame = stack.pop();

        // Check to see if there are any remaining suffixes to output.
        if (frame.index < frame.suffixNames.length) {
          var toReturn = frame.prefix + unsafe(frame.suffixNames[frame.index]);

          if (!peek) {
            frame.index++;
          }

          // If the current frame has more suffixes, retain it on the stack.
          if (frame.index < frame.suffixNames.length) {
            stack.push(frame);

            // Otherwise, put all of the subtrees on the stack.
          } else {
            for (key in frame.subtrees) {
              if (key.indexOf(':') != 0) {
                continue;
              }
              var target = frame.prefix + unsafe(key);
              var subtree = frame.subtrees[key];
              this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree$PrefixTreeIterator::addTree(Lde/verism/client/components/suggestBox/patch/PatchedPrefixTree;Ljava/lang/String;)(subtree, target);
            }
          }

          return toReturn;

       // Put all subframes on the stack, and return to top of loop.
       } else {
         for (var key in frame.subtrees) {
           if (key.indexOf(':') != 0) {
             continue;
           }
           var target = frame.prefix + unsafe(key);
           var subtree = frame.subtrees[key];

           this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree$PrefixTreeIterator::addTree(Lde/verism/client/components/suggestBox/patch/PatchedPrefixTree;Ljava/lang/String;)(subtree, target);
         }
       }
     }

     // This would indicate that next() was called on an empty iterator.
     // Will throw an exception from next().
     return null;
    }-*/;
  }

  /**
   * Used by native methods to create an appropriately blessed PrefixTree.
   * 
   * @param prefixLength Smaller prefix length equals faster, more direct
   *          searches, at a cost of setup time
   * @return a newly constructed prefix tree
   */
  protected static PatchedPrefixTree createPrefixTree(int prefixLength) {
    return new PatchedPrefixTree(prefixLength);
  }

  /**
   *  Ensure that a String can be safely used as a key to an associative-array
   *  JavaScript object by prepending a prefix.
   *  
   *  @param s The String to make safe
   *  @return A safe version of <code>s</code>
   */
  private static String safe(String s) {
    return ':' + s;
  }

  /**
   *  Undo the operation performed by safe().
   *  
   *  @param s A String returned from safe()
   *  @return The original String passed into safe()
   */
  private static String unsafe(String s) {
    return s.substring(1);
  }

  /**
   * Stores the requested prefix length.
   */
  protected final int prefixLength;

  /**
   * Field to store terminal nodes in.
   */
  protected JavaScriptObject suffixes;

  /**
   * Field to store subtrees in.
   */
  protected JavaScriptObject subtrees;

  /**
   * Store the number of elements contained by this PrefixTree and its
   * sub-trees.
   */
  protected int size = 0;

  /**
   * Constructor.
   */
  public PatchedPrefixTree() {
    this(2, null);
  }

  /**
   * Constructor.
   *
   * @param source Initialize from another collection
   */
  public PatchedPrefixTree(Collection<String> source) {
    this(2, source);
  }

  /**
   * Constructor.
   *
   * @param prefixLength Smaller prefix length equals faster, more direct
   *          searches, at a cost of setup time.
   */
  public PatchedPrefixTree(final int prefixLength) {
    this(prefixLength, null);
  }

  /**
   * Constructor.
   *
   * @param prefixLength Smaller prefix length equals faster, more direct
   *          searches, at a cost of setup time.
   * @param source Initialize from another collection
   */
  public PatchedPrefixTree(final int prefixLength, final Collection<String> source) {
    this.prefixLength = prefixLength;
    clear();

    if (source != null) {
      addAll(source);
    }
  }

  /**
   * Add a String to the PrefixTree.
   *
   * @param s The data to add
   * @return <code>true</code> if the string was added, <code>false</code>
   *         otherwise
   */
  @Override
  public native boolean add(String s) /*-{
    var suffixes =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suffixes;
    var subtrees =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::subtrees;
    var prefixLength =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::prefixLength;

    // This would indicate a mis-use of the code.
    if ((s == null) || (s.length == 0)) {
      return false;
    }

    // Use <= so that strings that are exactly prefixLength long don't
    // require some kind of null token.
    if (s.length <= prefixLength) {
      var safeKey = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::safe(Ljava/lang/String;)(s);
      if (suffixes.hasOwnProperty(safeKey)) {
        return false;
      } else {
        // Each tree keeps a count of how large it and its children are.
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::size++;

        suffixes[safeKey] = true;
        return true;
      }

    // Add the string to the appropriate PrefixTree.
    } else {
      var prefix = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::safe(Ljava/lang/String;)(s.slice(0, prefixLength));
      var theTree;

      if (subtrees.hasOwnProperty(prefix)) {
        theTree = subtrees[prefix];
      } else {
        theTree = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::createPrefixTree(I)(prefixLength << 1);
        subtrees[prefix] = theTree;
      }

      var slice = s.slice(prefixLength);
      if (theTree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::add(Ljava/lang/String;)(slice)) {
        // The size of the subtree increased, so we need to update the local count.
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::size++;
        return true;
      } else {
        return false;
      }
    }
  }-*/;

  /**
   * Initialize native state.
   */
  @Override
  public native void clear() /*-{
    this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::size = 0;
    this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::subtrees = {};
    this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suffixes = {};
  }-*/;

  @Override
  public boolean contains(Object o) {
    if (o instanceof String) {
      return contains((String) o);
    } else {
      return false;
    }
  }

  public boolean contains(String s) {
    return (getSuggestions(s, 1)).contains(s);
  }

  /**
   * Retrieve suggestions from the PrefixTree. The number of items returned from
   * getSuggesstions may slightly exceed <code>limit</code> so that all
   * suffixes and partial stems will be returned. This prevents the search space
   * from changing size if the PrefixTree is used in an interactive manner.
   * <br/> The returned List is guaranteed to be safe; changing its contents
   * will not affect the PrefixTree.
   *
   * @param search The prefix to search for
   * @param limit The desired number of results to retrieve
   * @return A List of suggestions
   */
  public List<String> getSuggestions(String search, int limit) {
    final List<String> toReturn = new ArrayList<String>();
    if ((search != null) && (limit > 0)) {
      suggestImpl(search, "", toReturn, limit);
    }
    return toReturn;
  }

  @Override
  public Iterator<String> iterator() {
    return new PrefixTreeIterator(this);
  }

  /**
   * Get the number of all elements contained within the PrefixTree.
   * 
   * @return the size of the prefix tree
   */
  @Override
  public int size() {
    return size;
  }

  protected native void suggestImpl(String search, String prefix,
      Collection<String> output, int limit) /*-{
    var suffixes =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suffixes;
    var subtrees =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::subtrees;
    var prefixLength =
        this.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::prefixLength;

    // Search is too big to be found in current tree, just recurse.
    if (search.length > prefix.length + prefixLength) {
      var key = @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::safe(Ljava/lang/String;)(search.slice(prefix.length, prefix.length + prefixLength));

      // Just pick the correct subtree, if it exists, and call suggestImpl.
      if (subtrees.hasOwnProperty(key)) {
        var subtree = subtrees[key];
        var target = prefix + @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)(key);
        subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suggestImpl(Ljava/lang/String;Ljava/lang/String;Ljava/util/Collection;I)(search, target, output, limit);
      }

    // The answer can only exist in this tree's suffixes or subtree keys.
    } else {
     // Check local suffixes.
     for (var suffix in suffixes) {
       if (suffix.indexOf(':') != 0) {
         continue;
       }
       var target = prefix + @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)(suffix);
       if (target.indexOf(search) == 0) {
         output.@java.util.Collection::add(Ljava/lang/Object;)(target);
       }

       if (output.@java.util.Collection::size()() >= limit) {
         return;
       }
     }

     // Check the subtree keys.  If the key matches, that implies that all
     // elements of the subtree would match.
     for (var key in subtrees) {
       if (key.indexOf(':') != 0) {
         continue;
       }
       var target = prefix + @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)(key);
       var subtree = subtrees[key];

       // See if the prefix gives us a match.
       if (target.indexOf(search) == 0) {

         // Provide as many suggestions as will fit into the remaining limit.
         // If there is only one suggestion, include it.
         if ((subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::size <= limit - output.@java.util.Collection::size()()) ||
             (subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::size == 1)) {
           subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::dump(Ljava/util/Collection;Ljava/lang/String;)(output, target);

         // Otherwise, include as many answers as we can by truncating the suffix
         } else {

           // Always fully-specify suffixes.
           for (var suffix in subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::suffixes) {
             if (suffix.indexOf(':') == 0) {
               output.@java.util.Collection::add(Ljava/lang/Object;)(target + @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)(suffix));
             }
           }

           // Give the keys of the subtree.
           for (var subkey in subtree.@de.verism.client.components.suggestBox.patch.PatchedPrefixTree::subtrees) {
             if (subkey.indexOf(':') == 0) {
               output.@java.util.Collection::add(Ljava/lang/Object;)(target + @de.verism.client.components.suggestBox.patch.PatchedPrefixTree::unsafe(Ljava/lang/String;)(subkey) + "...");
             }
           }
         }
       }
     }
   }
  }-*/;

  /**
   * Put all contents of the PrefixTree into a Collection.
   * 
   * @param output the collection into which the prefixes will be dumped
   * @param prefix the prefix to filter with
   */
  private void dump(Collection<String> output, String prefix) {
    for (String s : this) {
      output.add(prefix + s);
    }
  }
}
