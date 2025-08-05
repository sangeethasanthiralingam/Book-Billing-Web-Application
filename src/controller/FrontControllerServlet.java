@@ .. @@
             case "generate-bill":
                 billingController.handleGenerateBill(request, response);
                 break;
+            case "cancel-bill":
+                billingController.handleCancelBill(request, response);
+                break;
+            case "quick-bill":
+                billingController.handleQuickBill(request, response);
+                break;
+            case "bill-search":
+                billingController.handleBillSearch(request, response);
+                break;
                 
             // Dashboard routes
             case "dashboard":