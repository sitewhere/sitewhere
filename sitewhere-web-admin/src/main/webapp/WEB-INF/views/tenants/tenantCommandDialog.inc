<!-- Dialog for monitoring progress for a tenant command -->
<div id="cmd-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3>Stop Tenant</h3>
	</div>
	<div class="modal-body">
		<div id="cmd-task-name"
			style="text-align: center; font-size: 20px; margin-bottom: 20px;"></div>
		<div id="globalBar" style="width: 100%"></div>
		<div id="cmd-current-operation"
			style="text-align: center; font-size: 14px;">---</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal"
			data-i18n="public.Cancel"></a> <a id="cmd-dialog-close"
			href="javascript:void(0)" class="btn btn-primary">Close</a>
	</div>
</div>

<!-- Script support for Cluster Manager upload dialog -->
<script>
	/** Global progress bar */
	var globalBar;

	$(document).ready(function() {
		globalBar = $("#globalBar").kendoProgressBar({
			type : "percent",
			animation : false
		}).data("kendoProgressBar");

		$("#cmd-dialog-close").click(function() {
			$('#cmd-dialog').modal('hide');
		});
	});

	/** Open the dialog */
	function cmdOpen() {
		$('#cmd-dialog').modal('show');

		// Reset progress.
		var startProgress = {
			"taskName" : "",
			"currentOperation" : 0,
			"totalOperations" : 1,
			"message" : ""
		}
		cmdUpdate(startProgress);
	}

	/** Update UI with progress */
	function cmdUpdate(progress) {
		var globalPercent = Math.abs(progress.currentOperation * 100 / progress.totalOperations);
		globalBar.value(globalPercent);

		$("#cmd-task-name").html(progress.taskName);
		$("#cmd-current-operation").html("<div style='margin: 20px; font-size: 18px;'>" + progress.message + "</div>");
	}

	/** Close the dialog */
	function cmdClose() {
		$('#cmd-dialog').modal('hide');
	}
</script>