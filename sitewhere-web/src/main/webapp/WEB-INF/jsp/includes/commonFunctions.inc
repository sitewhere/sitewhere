<script>
	/** Common callback function for async functions */
	var swSuccessCallback;

	/** Delete a specification based on its token */
	function swSpecificationDelete(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Delete Specification", "Are you sure that you want to delete device specification with " +
			"token '" + token + "'?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/specifications/"
					+ token + "?force=true", swSpecificationDeleteSuccess, swSpecificationDeleteFail);
			}
		});
	}

	/** Handle successful specification delete */
	function swSpecificationDeleteSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed specification delete */
	function swSpecificationDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete specification.");
	}

	/** Delete a device based on its hardware id */
	function swDeviceDelete(hardwareId, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Delete Device", "Are you sure that you want to delete device with hardware id '" + hardwareId + 
			"'?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/devices/"
					+ hardwareId + "?force=true", swDeviceDeleteSuccess, swDeviceDeleteFail);
			}
		});
	}

	/** Handle successful device delete */
	function swDeviceDeleteSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed device delete */
	function swDeviceDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete device.");
	}

	/** Delete a device assignment based on its token */
	function swAssignmentDelete(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Delete Assignment", "Are you sure you want to delete this device assignment?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/assignments/"
					+ token + "?force=true", swAssignmentDeleteSuccess, swAssignmentDeleteFail);
			}
		});
	}

	/** Handle successful device assignment delete */
	function swAssignmentDeleteSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed device assignment delete */
	function swAssignmentDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete device assignment.");
	}

	/** Release an assignment based on its unique token */
	function swReleaseAssignment(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Release Assignment", "Are you sure you want to release this device assignment?", function(result) {
			if (result) {
				$.postJSON("${pageContext.request.contextPath}/api/assignments/"
					+ token + "/end", null, swReleaseSuccess, swReleaseFail);
			}
		});
	}

	/** Handle successful assignment release */
	function swReleaseSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed assignment release */
	function swReleaseFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to release assignment.");
	}

	/** Report assignment missing based on its unique token */
	function swAssignmentMissing(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Report Missing", "Are you sure you want to report this assignment missing?", function(result) {
			if (result) {
				$.postJSON("${pageContext.request.contextPath}/api/assignments/"
					+ token + "/missing", null, swAssignmentMissingSuccess, swAssignmentMissingFail);
			}
		});
	}

	/** Handle successful missing report */
	function swAssignmentMissingSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed missing report */
	function swAssignmentMissingFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to mark assignment as missing.");
	}

	/** Delete a zone based on its unique token */
	function swZoneDelete(zoneToken, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm("Delete Zone", "Are you sure you want to delete this zone?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/zones/"
					+ zoneToken + "?force=true", swZoneDeleteSuccess, swZoneDeleteFail);
			}
		});
	}

	/** Handle successful zone delete */
	function swZoneDeleteSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed zone delete */
	function swZoneDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete zone.");
	}
	
	/** Gets a string array as a comma-delimited string */
	function swArrayAsCommaDelimited(input) {
		var result = "";
		if (!input) {
			return result;
		}
		for (var i =0; i<input.length; i++) {
			if (i != 0) {
				result += ", ";
			}
			result += input[i];
		}
		return result;
	}
</script>