if (assignment != null) {
	if (assignment.metadata['serialNumber'] == '59eb6c97-9fe6-4020-ad56-b11d4cc6f78f') {
		logger.warn("Not gonna send this one!");
		return true;
	}
	return false;
} else {
	return true;
}