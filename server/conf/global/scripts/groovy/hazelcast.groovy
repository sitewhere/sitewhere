// ###################### //
// Hazelcast Example Code //
// ###################### //

// Access a map shared across the memory grid.
com.hazelcast.core.IMap<String, String> map = hazelcast.getMap('documents')

// Attempt to access an item from the map.
def doc = map.get('docid-123')
if (doc == null) {
	// Add an item if not already there.
	map.put 'docid-123', 'Document data stored in a Hazelcast map'
} else {
	// Print the value if found.
	logger.info "Found a document: ${doc}"
}
