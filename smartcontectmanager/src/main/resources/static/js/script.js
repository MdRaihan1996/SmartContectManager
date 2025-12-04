console.log("This is script file")
const toggleSidebar = () => {
	
	if($(".sidebar").is(":visible")){
		
		$(".sidebar").css("display","none")
		$(".sidebar").css("margin-left","0%")
	}else{
		
		$(".sidebar").css("display","block")
		$(".sidebar").css("margin-left","20%")
	}
};

const search = () => {
	
	//console.log("searching....");
	
	let query=$("search-input").val();
	//console.log(query);
	
	if(query==""){
		$(".search-result").hide();
		
	}else{
		console.log(query);
		
		let url=`http://localhost:8080/search/${query}`;
		
		fetch(url).then(response=>{
			
			return response.json();
			
		}).then((data) =>{
			
			//console.log(data);
			
			let text=`<div class='list-group'>`;
			
			data.forEach((contact)=>{
				
				text+=`<a href='#' class='list-group-item list-group-action'> ${contact.name}</a>`
			});
			 
			text+=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
			
		});
		
		
	}
};

//first request for server to create order
//payment start when //onclick" paymentStart" function that is call in Html page 
const paymentStart=() => {


	console.log("payment started.....");
	let amount= $("#payment_field").val();
	console.log(amount);

	if(amount==""||amount==null){

		swal("Please enter payment", "amount is required!!", "error");
		return;
	}

	//we will use ajex to send request to server to create order ajex

	$.ajax({
		
      url:"/user/create_order",  //send request to server handler
	  data:JSON.stringify({amount:amount,info:'order_request'}),     //send ammount data to server
	  contentType:"application/json",  //send jason data to server so we define content type
	  type:"Post",                   //request send in Post form
	  dataType:"json",
	  success:function(response){    //call back function when request successfully send then it invoke
		//invoked when success
		console.log(response);

		if(response.status=="created"){

			//open payment form
//lets create following a varibale 
			let options={

				key:"rzp_test_gt30kVDiLL2Cu6",
				amount: response.amount,
				currency: "INR",
				name:"smart contact payment",
				description:"Donation",
				Image:"https://yt3.ggpht.com/h5GQrHvmmOeneN9Wa7RlEBz8ADQwhQu7TsOX1NNRiFgfrVmAwYWxu5kCrdWowJV5sHd5SpizPf4=s48-c-k-c0x00ffffff-no-rj",
				order_id: response.id,

				handler:function (response){
					console.log(response.razorpay_payment_id);
					console.log(response.razorpay_order_id);
					console.log(response.razorpay_signature);
					console.log("Payment successfull..");

					updatePaymentOnServer(response.razorpay_payment_id,
					response.razorpay_order_id,'paid');

					swal("Good job!", "congrate!!your payment is succesfully pay", "success");
					
				},
				//alreday fill data come for prfill function
				prefill: {
					 name:"",
					 email:"",
                    contact: "",
				},

				notes: {

					address:"smart contact manager is my personal website",
				},
				theme:{
					color:"#3399cc",
				},
			};

			let rzp1 = new Razorpay(options);

			rzp1.on("payment.failed",function(response){

				console.log(response.error.code);
				console.log(response.error.description);
				console.log(response.error.source);
				console.log(response.error.step);
				console.log(response.error.reason);
				console.log(response.error.code.metdata.order_id);
				console.log(response.error.code.metdata.payment_id);
				alert("Opps! payment is failed");
				swal("Faield!!", "Opps! payment is failed", "error");

				
			});
			rzp1.open();
		}
	  },

	  error:function(error){
// invoked when error
console.log(error);
alert("something went worng!!");
	  },
	});
};


function updatePaymentOnServer(payment_id,order_id,status){
	$.ajax({
		url:"/user/update_order",
		data:JSON.stringify({payment_id: payment_id,
			order_id: order_id,
			status:status}),

		contentType:"application/json",
		type:"Post",
		dataType:"json",

		success:function(response){

			swal("Good job!", "congrate!!your payment is succesfully pay", "success");
		},

		error:function(error){

			swal("faield!", "congrate!!your payment is succesfully pay, But we did not get on server we will contact you", "success");
		},
	});
}