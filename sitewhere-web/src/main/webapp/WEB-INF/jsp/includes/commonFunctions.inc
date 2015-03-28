<script>
	/** Common callback function for async functions */
	var swSuccessCallback;

	/** Delete a specification based on its token */
	function swSpecificationDelete(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("public.DeleteSpecification"), i18next("includes.commonFunctions.AYSTYWTDDSW") +" "+
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTDS"));
	}

	/** Delete a device based on its hardware id */
	function swDeviceDelete(hardwareId, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("includes.commonFunctions.DD"), i18next("includes.commonFunctions.AYSTYWTDDWD")+" id "+ hardwareId + 
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTDD"));
	}

	/** Delete a device assignment based on its token */
	function swAssignmentDelete(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("public.DeleteAssignment"), i18next("includes.commonFunctions.AYSYWTDTDA"), function(result) {
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTDDA"));
	}

	/** Release an assignment based on its unique token */
	function swReleaseAssignment(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("includes.commonFunctions.RA"), i18next("includes.commonFunctions.AYSYWTRTDA"), function(result) {
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTRA"));
	}

	/** Report assignment missing based on its unique token */
	function swAssignmentMissing(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("includes.ReportMissing"), i18next("includes.commonFunctions.AYSYWTRTAM"), function(result) {
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTMAAM"));
	}

	/** Delete a zone based on its unique token */
	function swZoneDelete(zoneToken, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("includes.DeleteZone"), i18next("includes.commonFunctions.AYSYWTDTZ"), function(result) {
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
		handleError(jqXHR, i18next("includes.commonFunctions.UTDZ"));
	}

	/** Delete a device group based on its token */
	function swDeviceGroupDelete(token, successCallback) {
		swSuccessCallback = successCallback;
		swConfirm(i18next("includes.DeleteDeviceGroup"), i18next("includes.commonFunctions.AYSYWTDDGWT")+" '" + token + 
			"'?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/devicegroups/"
					+ token + "?force=true", swDeviceGroupDeleteSuccess, swDeviceGroupDeleteFail);
			}
		});
	}

	/** Handle successful device group delete */
	function swDeviceGroupDeleteSuccess() {
		if (swSuccessCallback != null) {
			swSuccessCallback();
		}
	}

	/** Handle failed device group delete */
	function swDeviceGroupDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, i18next("includes.commonFunctions.UTDDG"));
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